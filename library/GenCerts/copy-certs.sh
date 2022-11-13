#!/bin/bash

# Create certificates folder if it doesn't exist
mkdir -p ~/.AgentStation/certificates

echo "Copying CA's self-signed certificate"
cp ca-cert.pem ~/.AgentStation/certificates

echo "Copying client's certificate signing request (CSR)"
cp client-cert.pem ~/.AgentStation/certificates

echo "Copying client's private key"
cp client-key.pem ~/.AgentStation/certificates

echo "Copying server's certificate signing request (CSR)"
cp server-cert.pem ~/.AgentStation/certificates

echo "Copying server's private key"
cp server-key.pem ~/.AgentStation/certificates

