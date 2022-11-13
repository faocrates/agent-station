#!/bin/bash

# Remove any previous .pem files
rm -f -- *.pem

# 1. Generate CA's private key and self-signed certificate
openssl req -x509 -newkey rsa:4096 -days 365 -nodes -keyout ca-key.pem -out ca-cert.pem -subj "/C=GB/ST=Hampshire/L=Southampton/O=YourOrganisation/OU=IT/CN=site.co.uk/emailAddress=youremail@site.co.uk"

echo "CA's self-signed certificate"
openssl x509 -in ca-cert.pem -noout -text

# 2. Generate server's private key and certificate signing request (CSR)
openssl req -newkey rsa:4096 -nodes -keyout server-key.pem -out server-req.pem -subj "/C=GB/ST=Hampshire/L=Southampton/O=Agent Station Server/OU=Server Node/CN=site.co.uk/emailAddress=youremail@site.co.uk"

# 3. Use CA's private key to sign server's CSR and get back the signed certificate
openssl x509 -req -in server-req.pem -days 90 -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out server-cert.pem -extfile server-ext.cnf

echo "Server's signed certificate"
openssl x509 -in server-cert.pem -noout -text

# 4. Generate client's private key and certificate signing request (CSR)
openssl req -newkey rsa:4096 -nodes -keyout client-key.pem -out client-req.pem -subj "/C=GB/ST=Hampshire/L=Southampton/O=Agent Station Client/OU=Client Node/CN=site.co.uk/emailAddress=youremail@site.co.uk"

# 5. Use CA's private key to sign client's CSR and get back the signed certificate
openssl x509 -req -in client-req.pem -days 90 -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out client-cert.pem -extfile client-ext.cnf

echo "Client's signed certificate"
openssl x509 -in client-cert.pem -noout -text
