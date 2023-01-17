#!/bin/bash

cd module-cleaner
javac ModuleCleaner.java && java ModuleCleaner $1 $2
rm ModuleCleaner.class
cd ..