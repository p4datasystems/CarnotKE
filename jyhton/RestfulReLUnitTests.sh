ant clean
sudo rm -rf ./dist
ant
ant
sudo pip install --target=/Users/pcannata/Mine/Carnot/CarnotKE/jyhton/dist/Lib/ Flask
echo ""
echo "You can now:"
echo "scp -r -i /Users/pcannata/Mine/P4/p4datasystems.pem ../../Carnot.zip ubuntu@ec2-52-34-29-13.us-west-2.compute.amazonaws.com:"
echo "ssh -i /Users/pcannata/Mine/P4/p4datasystems.pem ubuntu@ec2-52-34-29-13.us-west-2.compute.amazonaws.com"
echo ""
sudo chown pcannata dist/Lib/
dist/bin/jython ../../CarnotRE/restful_start.py
