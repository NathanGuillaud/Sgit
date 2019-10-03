#!/bin/bash
cd /home/nathan/IdeaProjects/sgit/
sbt --error 'set showSuccess := false' "run $*"
