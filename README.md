# fairysupport run
It is a program that runs a local shell on multiple servers 

# project page 
<https://fairysupport.com/run/>

## Usage
### Require
java 1.7 or more 

### Install
Download com\_fairysupport\_run.jar(It is under demo) and place it in the appropriate folder 

### Prepare
Create a folder that stores main.sh in the same hierarchy as com\_fairysupport\_run.jar  
Create a server.properties in the same hierarchy as com\_fairysupport\_run.jar  

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

main.sh

```
#!/bin/bash

echo "Hello world"

```
### Run
```
#In case of Windows command prompt
chcp 65001

#Give the folder name where main.sh is stored as the first argument
java -jar com_fairysupport_run.jar sample

-----------------------------------------------------------------------------------------------------------------------
[start][127.0.0.3:22]
Please input user
user1
Please input password
user1password
[upload file][127.0.0.3:22]

....

[run main.sh][127.0.0.3:22]
cd /home/user1/com_fairysupport_run/sample && ./main.sh
Hello world
[delete file][127.0.0.3:22]

....

[end][127.0.0.3:22]
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
[start][127.0.0.2:22]
Please input user
user1
Please input password
user1password
Please input passphrase
user1passphrase
[upload file][127.0.0.2:22]

....

[run main.sh][127.0.0.2:22]
cd /home/user1/com_fairysupport_run/sample && ./main.sh
Hello world
[delete file][127.0.0.2:22]

....

[end][127.0.0.2:22]
-----------------------------------------------------------------------------------------------------------------------
```

## Description
Execute the following (1) (2) (3) on the server described in server.properties  
(1)Upload local files to server  
(2)run main.sh  
(3)Delete uploaded file  

### Examples
*Example1*  
Hello world 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample1
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

main.sh

```
#!/bin/bash

echo "Hello world"

for param in $*
do
    echo "${param}"
done

```

```
java -jar com_fairysupport_run.jar sample1 arg1 arg2

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample1 && ./main.sh arg1 arg2
Hello world
arg1
arg2
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example2*  
Multiple files 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample2
|       |-- child_dir
|       |   `-- child.sh
|       |-- hello.sh
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

hello.sh

```
#!/bin/bash

echo "Hello world"

```

child.sh

```
#!/bin/bash

echo "Hello child"

```

main.sh

```
#!/bin/bash

./hello.sh
./child_dir/child.sh

```

```
java -jar com_fairysupport_run.jar sample2

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample2 && ./main.sh
Hello world
Hello child
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example3*  
Multiple directories  
Write the necessary directory name in include.txt  

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   |-- common1
|   |   `-- child_dir
|   |       `-- common1.sh
|   |-- common2
|   |   `-- common2.sh
|   `-- sample3
|       |-- include.txt
|       |-- hello.sh
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

common1.sh

```
#!/bin/bash

echo "Hello common1"

```

common2.sh

```
#!/bin/bash

echo "Hello common2"

```

include.txt

```
../${1}
../common2

```

hello.sh

```
#!/bin/bash

echo "Hello world"

```

main.sh

```
#!/bin/bash

../common1/child_dir/common1.sh
../common2/common2.sh
./hello.sh

```

```
java -jar com_fairysupport_run.jar sample3 common1

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample3 && ./main.sh
Hello common1
Hello common2
Hello world
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example4*  
sudo

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample4
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

main.sh

```
#!/bin/bash

RUN_DIR=`pwd`
sudo -S touch ${RUN_DIR}/fairysupport_sample.txt
sudo -S sh -c "echo 'add text' >> ${RUN_DIR}/fairysupport_sample.txt"
sudo -S cat ${RUN_DIR}/fairysupport_sample.txt

```

```
java -jar com_fairysupport_run.jar sample4

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample4 && ./main.sh
add text
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example5*  
You can also write your user name, password, passphrase

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server_raw.properties
|   `-- sample5
|       `-- main.sh
```

```
java -jar com_fairysupport_run.jar --keygenerate

```

fairysupport\_run\_key.txt is generated

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- fairysupport_run_key.txt
|   |-- server_raw.properties
|   `-- sample5
|       `-- main.sh
```

server_raw.properties

```
server1.address=127.0.0.2
server1.port=22
server1.user=user1
server1.password=user1password
server1.keyPath=/path/id_rsa
server1.passphrase=user1passphrase
server2.address=127.0.0.3
server2.port=22

```

```
java -jar com_fairysupport_run.jar -k fairysupport_run_key.txt -f server_raw.properties -o server.properties

```

server.properties is generated

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- fairysupport_run_key.txt
|   |-- server_raw.properties
|   |-- server.properties
|   `-- sample5
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.user=user1
server1.password=xxxxxxx
server1.keyPath=/path/id_rsa
server1.passphrase=xxxxxx
server2.address=127.0.0.3
server2.port=22

```

password、passphrase is encrypted

main.sh

```
#!/bin/bash

echo "Hello world"

```

```
java -jar com_fairysupport_run.jar sample5 -k fairysupport_run_key.txt -f server.properties

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample5 && ./main.sh
Hello world
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example6*  
install Apache HTTP Server 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample6
|       |-- httpd.conf
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

main.sh

```
#!/bin/bash

RUN_DIR=`pwd`
sudo -S yum -y install httpd
sudo -S mv /etc/httpd/conf/httpd.conf /etc/httpd/conf/httpd.conf.bk
sudo -S cp ${RUN_DIR}/httpd.conf /etc/httpd/conf/httpd.conf
sudo -S chmod 644 /etc/httpd/conf/httpd.conf
sudo -S semanage fcontext -a -t httpd_config_t /etc/httpd/conf/httpd.conf
sudo -S restorecon -v /etc/httpd/conf/httpd.conf

```

```
java -jar com_fairysupport_run.jar sample6

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample6 && ./main.sh
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example7*  
run php 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample7
|       |-- sample.php
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

sample.php

```
<?php 
echo "Hello php"; 
echo PHP_EOL;

```

main.sh

```
#!/bin/bash

php ./sample.php

```

```
java -jar com_fairysupport_run.jar sample7

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample7 && ./main.sh
Hello php
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example8*  
bulk 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server1.properties
|   |-- server2.properties
|   |-- sample1
|   |   `-- main.sh
|   |-- sample2
|   |   |-- child_dir
|   |   |   `-- child.sh
|   |   |-- hello.sh
|   |   `-- main.sh
|   `-- sample8.txt
```

server1.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa

```

server2.properties

```
server2.address=127.0.0.3
server2.port=22

```

sample8.txt

```
sample1 ${1} arg2 -f server1.properties
sample2 -f server2.properties

```

```
java -jar com_fairysupport_run.jar sample8.txt arg1

-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample1 && ./main.sh arg1 arg2
Hello world
arg1
arg2
....
....
cd /home/user1/com_fairysupport_run/sample2 && ./main.sh
Hello world
Hello child
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example9*  
Give arguments interactively 

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample1
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

```
java -jar com_fairysupport_run.jar
Please input directory name or file name
sample1 arg1 arg2
-----------------------------------------------------------------------------------------------------------------------
....
cd /home/user1/com_fairysupport_run/sample1 && ./main.sh arg1 arg2
Hello world
arg1
arg2
....
-----------------------------------------------------------------------------------------------------------------------
```

*Example10*  
get file  
Write the necessary file or directory name in get.txt  

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample10
|       |-- get.txt
|       `-- main.sh
```

server.properties

```
server1.address=127.0.0.2
server1.port=22
server1.keyPath=/path/id_rsa
server2.address=127.0.0.3
server2.port=22

```

get.txt

```
sample1.txt ../sample10_result/${FILE}_${SERVER}_local_sample.txt
../hello ../sample10_result/${FILE}_${SERVER}_local_hello
${1}.txt ../sample10_result/${FILE}_${SERVER}_local_${1}.txt

```

main.sh

```
#!/bin/bash

echo "hello1" > sample1.txt
mkdir ../hello
echo "hello2" > ../hello/sample2.txt
echo "hello3" > ${1}.txt

```

```
java -jar com_fairysupport_run.jar sample10 arg1

-----------------------------------------------------------------------------------------------------------------------
[start][127.0.0.3:22]
....

[get file][127.0.0.3:22]
download sample1.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server2_local_sample.txt
download ../hello/sample2.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server2_local_hello\sample2.txt
download arg1.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server2_local_arg1.txt

....

[end][127.0.0.3:22]
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
[start][127.0.0.2:22]

....

[get file][127.0.0.2:22]
download sample1.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server1_local_sample.txt
download ../hello/sample2.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server1_local_hello\sample2.txt
download arg1.txt -> xxxxxxxxxx\sample10\..\sample10_result\server_server1_local_arg1.txt

....

[end][127.0.0.2:22]
-----------------------------------------------------------------------------------------------------------------------
```

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   |-- sample10
|   |   |-- get.txt
|   |   |-- main.sh
|   `-- sample10_result
|       |-- server_server1_local_hello
|       |   `-- sample2.txt
|       |-- server_server2_local_hello
|       |   `-- sample2.txt
|       |-- server_server1_local_arg1.txt
|       |-- server_server1_local_sample.txt
|       |-- server_server2_local_arg1.txt
|       `-- server_server2_local_sample.txt
```

## License
MIT