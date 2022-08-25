#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/$section$.routes"
echo "" >> ../conf/$section$.routes
echo "GET        /$className;format="decap"$                       controllers.$section$.$className$Controller.onPageLoad" >> ../conf/$section$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"
