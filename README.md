# KafkaStreams
Processing and storing real time streaming data.


## bankBalance :
 * This example is for demonstrating *Exactly-Once semantics*.  
 * Use kafka producer to send transtion like :  
      {"Name": "JackD", "amount": 500, "time": "2017-09-21T05:23:55"}
 * Target is to have a KStreams application that takes a transaction like above and updates amount  
   and time when the update was received.  
   
## colour-count : 
 * push comma separated input using kafka producer to an input topic like userid, colour.
 * filter out all colours other than green, red and blue.
 * get *running count* of colour input overall and output this to a new kafka topic.
 * example:   
      jay, blue  
      robin, red  
      jay, green  
      alice, green      
   output colour count is :  
      (blue, 0)  
      (red, 1)  
      (green , 2)   
 * The way to think is that as soon as a new entry comes with an existing key, we delete the last entry((key,value) pair) and accept the new entry. So
      jay, blue      ---->  robin, red     ----> (red,1)
      robin, red            jay, green           (green,2) 
                            alice, green         (blue, 0)

      jay, blue      ---->  robin, red    ----> robin, red   ---> (red,2)    
      robin, red            jay, green          alice, red        (green, 0) 
                            alice, green        jay, blue         (blue, 1) 
                                                 
## user-event-application :      
  * Join User purchases (KStream) to User data (Global KTable) generating a new KStream.
  * Target is to enrich User purchases with the User data present and write this result to a new kafka topic.  
  * Try to show the difference between an inner join and a left join. For inner join, value from either User purchases or User 
    data is not null which is not true during left join as User data may be null.    
  * Used kafka producer to show different case scenarios.  

## word-count :
  *  Create a map using KStream data as -->  <null, "Test tester Testing test">  
  *  MapValues to lowercase as  --> <null, "test tester testing test">  
  *  Split on space as -->  <null, "test">, <null, "tester">, <null, "testing">, <null, "test">    
  *  Use selectKey to make key same as value -->  <"test", "test">, <"tester", "tester">, <"testing", "testing">, <"test", 
     "test">    
  *  This is shuffle step as in mapReduce by using GroupByKey as --> (<"test", "test">, <"test", "test">), (<"tester",  
     "tester">), (<"testing", "testing">)
  *  Count occurence in each group --> <"test", 2>, <"tester", 1>, <"testing", 1>   
  
  <p align = "center">
  <img width="600" alt="screen shot 2018-05-16 at 11 09 53 am" src = "https://user-images.githubusercontent.com/15849566/40135273-c7171b88-58f9-11e8-8641-6f7b33c0e220.png">
  </p>

  
  
