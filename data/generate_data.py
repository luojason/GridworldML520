import subprocess
import shutil
import sys
import os.path as path
import numpy as np


def main():
    # parse command line arguments
    project = sys.argv[1]  # set to project1, project2, or project3
    prefix = sys.argv[2]  # string prepended to output filenames
    x = int(sys.argv[3])
    y = int(sys.argv[4])
    num_iter = sys.argv[5]

    # dict used to store state representation shapes for each project
    shape = {'project1': (y, x),
             'project2': (2, y, x),
             'project3': (2, y, x)}

    # run java simulation
    java_path = shutil.which('java')
    class_path = path.abspath('../old_projects/class')
    cmd = [java_path, '-cp', class_path, f'{project}.Main', str(x), str(y), num_iter]
    p = subprocess.run(cmd, capture_output=True, text=True, check=True)
    states, actions = p.stdout, p.stderr

    # encode data into numpy arrays
    state_array = np.fromstring(states, sep=' ').reshape((-1, *shape[project]))
    action_array = np.fromstring(actions, sep=' ')

    # write output
    np.save(f'{prefix}-inputs', state_array)
    np.save(f'{prefix}-outputs', action_array)


if __name__ == '__main__':
    main()
