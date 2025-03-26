#!/bin/bash

# Update system packages
sudo yum update -y

# Install Docker
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Create directory for application
mkdir -p /home/ec2-user/thinkr
cd /home/ec2-user/thinkr

# Copy environment variables
cp /home/ec2-user/.env .env

# Pull and start containers
docker-compose pull
docker-compose up -d 