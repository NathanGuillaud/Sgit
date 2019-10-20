#!/bin/bash
sbt package
chmod u+x sgit.sh
ln -s sgit.sh sgit
export PATH=$PATH:`pwd`
echo scala `pwd`/target/scala-2.12/sgit_2.12-0.1.jar \$\* > sgit.sh