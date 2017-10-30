Download APNtest.zip
=
- This includes the jar file **Test.jar**
- Developing ...

Usage example
=
- Some command usage examples

Cross Validation
-
| param | example |
| - | - |
| args[0] | -CV |
| args[1] | Directory path |
| args[2] | File name |
| args[3] | Max fold |
| args[4] | MEPA dividing num |
| args[5] | BioParams: [-ABC, -ACO, -PSO] |
| args[6] | Iteration |
| args[7] | Population |
| args[8] | ABC Params "employed:onlooker:scout" |

### **command usage example**

- **origin**

        java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5
- **ABC**

        java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -ABC 10 10 0.1:0.4:0.5
- **ACO**

        java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -ACO 10 10
- **PSO**

        java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -PSO 10 10

Train Test Validation
-
- Please **notice that** the training and testing data must to be in the same directory

| param | example |
| - | - |
| args[0] | -TT |
| args[1] | Directory path |
| args[2] | Train file name |
| args[3] | Test file name |
| args[4] | MEPA dividing num |
| args[5] | BioParams: [-ABC, -ACO, -PSO] |
| args[6] | Iteration |
| args[7] | Population |
| args[8] | ABC Params "employed:onlooker:scout" |

### **command usage example**

- **origin**

        java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5
- **ABC**

        java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -ABC 10 10 0.1:0.4:0.5
- **ACO**

        java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -ACO 10 10
- **PSO**

        java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -PSO 10 10
