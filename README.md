DelUndel
========
This program is for the recovery of deleted files, and for erasing all traces of files in the underlying filesystem so that they cannot be recovered. It is intended to be used on USB drives, not the hard drive of the computer it is running on. The name is based on the fact that the program's core features are deleting and un-deleting files, and is inspired by the origin of the word "modem" - modem = modulator/demodulator; delundel = deletion/undeletion.

Only FAT16 is currently (partially) implemented, and it only works with raw data dump files on one's computer. Use with physical USB drives is hopefully soon to come.

## Features ##
Crossed out = not implemented yet
 * ~~FAT12~~
 * FAT16
 * ~~FAT32~~
 * Raw data dump files
 * ~~Physical USB drives~~
 * Examining the contents of a file
 * Extracting a file from the "mounted" filesystem to the computer running the program
 * ~~Examining the contents of a deleted file~~
 * ~~Extracting a deleted file to the computer running the program~~
 * ~~Undeleting a deleted file~~
   * For the three above features, have to handle cases where parts of a deleted file were overridden
   * They can work for a file that is known to have _just_ been deleted, but it is better to just not use them yet
 * ~~Deleting a file~~
 * ~~Fully deleting all traces of a file~~
   * Not comfortable working on this until I know everything else works perfectly
 * Commandline interface
 * GUI interface (work in progress)
 * ~~Detection of USB drives to work with~~

This program is licensed under the MIT license.
