##!/bin/bash
#
#sudo yum update -y
#
#sudo amazon-linux-extras install docker
#
#sudo yum install docker
#
#sudo service docker start
#
#sudo usermod -a -G docker ec2-user
#
#docker info
#
#sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
#
#sudo chmod +x /usr/local/bin/docker-compose
#
#docker-compose version
#
#sudo chmod 666 /var/run/docker.sock
## shellcheck disable=SC2164
#cd /tmp && docker-compose up -d


#!/bin/bash
# Update the system
sudo yum update -y

sudo yum install expect -y

# Install Java 17 JDK
sudo amazon-linux-extras enable corretto8
sudo yum install -y java-17-amazon-corretto-devel

# Install MySQL
sudo yum install -y mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb

# intstall cloudwatch
sudo curl https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm -O
sudo rpm -U ./amazon-cloudwatch-agent.rpm


# mkdir /opt/deployment
sudo mkdir /opt/deployment
sudo chown -R $USER:$USER /opt/deployment

sudo chown -R $USER:$USER /opt/aws/amazon-cloudwatch-agent/etc

