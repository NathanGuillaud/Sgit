# Sgit

Git implementation in Scala.
 
#  Installation

```shell script
$ git clone https://github.com/NathanGuillaud/Sgit.git
$ cd Sgit
$ source install.sh
```

Now, you can run sgit command from everywhere !

#  Tests

To run tests, go to the sgit directory and run the following command:
```shell script
$ sbt test
```

# Features

## Create


- sgit init ✅

## Local changes

- sgit status ✅
- sgit diff ✅
- sgit add (filename or regexp) ✅
- sgit commit ✅

## Commit history

- sgit log ✅
- sgit log -p ✅
- sgit log —stat ✅

## Branches & tags

- sgit branch ✅
- sgit branch -av ✅
- sgit checkout ✅
- sgit tag ✅

## Merge & rebase

- sgit merge ❌
- sgit rebase ❌
- sgit rebase -i ❌
