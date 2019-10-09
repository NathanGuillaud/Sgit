#!/bin/bash
cd /home/nathan/IdeaProjects/sgit/
#sbt --error 'set showSuccess := false' "run $*"
scala target/scala-2.12/sgit_2.12-0.1.jar $*