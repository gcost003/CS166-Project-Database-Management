
<p align="center">
  <a href="" rel="noopener">
 <img width=200px height=200px src="https://previews.123rf.com/images/nasirkhan/nasirkhan1207/nasirkhan120700073/14520262-ilustraci%C3%B3n-de-wordcloud-de-dbms-database-management-system-.jpg" alt="Project logo"></a>
</p>

<h3 align="center">Group 27</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/kylelobo/The-Documentation-Compendium.svg)](https://github.com/kylelobo/The-Documentation-Compendium/pulls)

</div>

---



## 📝 Table of Contents

- [Getting Started](#getting_started)
- [Authors](#authors)

## 🏁 Getting Started <a name = "getting_started"></a>

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See [deployment](#deployment) for notes on how to deploy the project on a live system.

### Installing

A step by step series of examples that tell you how to get a development env running.
Once you copy the zip file of the our project and unzip them then you do the fillowing commands:

Run your database:
```
source ./startPostgreSQL.sh
```
```
source ./createPostgreDB.sh
```

Next go into your data dircotory and copy all the data onto the temporary PostgreDB. For example ours is $cd ./project/data/
```
cp itemStatus.csv /tmp/$USER/myDB/data/
```
```
cp users.csv /tmp/$USER/myDB/data/
```
```
cp menu.csv /tmp/$USER/myDB/data/
```
```
cp orders.csv /tmp/$USER/myDB/data/
```

Next run your create_db.sh file:
For example ours commands were
```
source ./project/sql/scripts/create_db.sh
```
Then run the compile.sh file to complie our Cafa.java code:
For example ours commands were
```
source ./project/java/scripts/compile.sh
```
  Now you are able to use our User Interface

When you are Done with database then please do remember to close the database:
```
source ./stopPostgreDB.sh
```

## ✍️ Authors <a name = "authors"></a>
>  [Giovanni Costagliola](), [Zhuonan Long]()

