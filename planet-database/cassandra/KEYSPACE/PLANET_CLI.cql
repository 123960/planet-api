CREATE KEYSPACE PLANET_CLI
  WITH REPLICATION = {
   'class' : 'SimpleStrategy', 
   'replication_factor' : 1 
  };