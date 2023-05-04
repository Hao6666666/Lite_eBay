#!/bin/bash

sudo systemctl restart mariadb
sudo systemctl restart myapp
sudo systemctl restart amazon-cloudwatch-agent