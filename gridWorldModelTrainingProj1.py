import tensorflow as tf
import numpy as np
import os

print(tf.config.list_physical_devices('GPU'))
checkpoint_path = "Project1/cp.ckpt"
checkpoint_dir = os.path.dirname(checkpoint_path)
EPOCHS_DEFAULT = 300

def generate_confusion_matrix( data, labels ):
    mat = [ [ 0 for i in range(4) ] for j in range(4) ]
    
    predictions = np.argmax( model.predict( data ), axis = 1 )
    
    for i in range( data.shape[0] ):
        mat[ labels[i] ][ predictions[i] ] += 1
    
    for i in range(4):
        print( "\t".join( [ str(c) for c in mat[i] ] ) )

def getTrainingData():
	trainData = np.load("C:/Users/vinst/OneDrive/Documents/project520/Project1/0-p1-training-inputs.npy") 
	trainDataLabels = np.load("C:/Users/vinst/OneDrive/Documents/project520/Project1/0-p1-training-outputs.npy")
	train_in = np.reshape(trainData, (-1, 100, 100) )
	train_out = tf.keras.utils.to_categorical(trainDataLabels, 4)
	return (train_in, train_out)

def getValidationData():
	testData = np.load("C:/Users/vinst/OneDrive/Documents/project520/Project1/1-p1-testing-inputs.npy") 
	testDataLabels = np.load("C:/Users/vinst/OneDrive/Documents/project520/Project1/1-p1-testing-outputs.npy")
	test_in = np.reshape(testData, (-1, 100, 100) )
	test_out = tf.keras.utils.to_categorical(testDataLabels, 4)
	return (test_in, test_out)

def buildModel():
	global EPOCHS_DEFAULT
	gridInput = tf.keras.layers.Input(shape = (100,100))
	flatten_image = tf.keras.layers.Flatten()( gridInput )
	dense_1 = tf.keras.layers.Dense( units = 5000, activation = tf.nn.relu )( flatten_image )
	dense_2 = tf.keras.layers.Dense( units = 2000, activation = tf.nn.relu )( dense_1 )
	dense_3 = tf.keras.layers.Dense( units = 1000, activation = tf.nn.relu )( dense_2 )
	dense_4 = tf.keras.layers.Dense( units = 100, activation = tf.nn.relu )( dense_3 )
	infDec = tf.keras.layers.Dense( units = 4, activation = None )( dense_4 )
	probabilities = tf.keras.layers.Softmax()( infDec )

	model = tf.keras.Model( inputs = gridInput, outputs = probabilities)
	model.compile( optimizer = 'adam', loss = 'categorical_crossentropy', metrics = ['accuracy'] )
	latest = tf.train.latest_checkpoint(checkpoint_dir)
	if(latest == None):
		return model
	
	model.load_weights(latest)
	EPOCHS_DEFAULT = 100
	return model

cp_callback = tf.keras.callbacks.ModelCheckpoint(filepath=checkpoint_path,
                                                 save_weights_only=True,
                                                 verbose=1)
x,y = getTrainingData()
model = buildModel()
history = model.fit(x , y, epochs = EPOCHS_DEFAULT,
          callbacks=[cp_callback] )

generate_confusion_matrix( xV, yV)

