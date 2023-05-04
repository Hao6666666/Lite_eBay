#!/usr/bin/expect


set timeout 10




spawn sudo mysql_secure_installation
expect "Enter current password for root (enter for none): "
send "\n"
expect "Set root password? "
send "Y\n"
expect "New password:"
send "root\n"
expect "Re-enter new password:"
send "root\n"
expect "Remove anonymous users? "
send "Y\n"
expect "Disallow root login remotely? "
send "Y\n"
expect "Remove test database and access to it? "
send "Y\n"
expect "Reload privilege tables now? "
send "Y\n"
send "\n"
send "exit"
send "\n"
expect eof