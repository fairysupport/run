# fairysupport run
ローカルにあるシェルを複数サーバで実行するプログラムです

# project page 
<https://ja.fairysupport.com/run/>

## Usage
### Require
java1.7以上が必要です

### Install
com\_fairysupport\_run.jar(demo配下にあります)をダウンロードし、適当なフォルダに置いてください

### Prepare
main.shが格納されたフォルダをcom\_fairysupport\_run.jarと同階層に作成する  
server.propertiesをcom\_fairysupport\_run.jarと同階層に作成する

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
#Windowsのコマンドプロンプトの場合
chcp 65001

#第一引数にmain.shが格納されたフォルダ名を与える
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
server.propertiesに記載されたサーバに対して下記(1)(2)(3)を行います  
(1)ローカルのファイルをサーバにアップロード  
(2)main.shを実行  
(3)アップロードしたファイルを削除  

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
include.txtに必要なディレクトリ名を書きます  

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
../common1
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
java -jar com_fairysupport_run.jar sample3

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
ユーザ名、パスワード、パスフレーズを書いておくこともできます

```
|-- com_fairysupport_run
|   |-- com_fairysupport_run.jar
|   |-- server.properties
|   `-- sample5
|       `-- main.sh
```

server.properties

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

main.sh

```
#!/bin/bash

echo "Hello world"

```

```
java -jar com_fairysupport_run.jar sample5

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
sample1 arg1 arg2 -f server1.properties
sample2 -f server2.properties

```

```
java -jar com_fairysupport_run.jar sample8.txt

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
対話的に引数を与える 

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

## License
MIT