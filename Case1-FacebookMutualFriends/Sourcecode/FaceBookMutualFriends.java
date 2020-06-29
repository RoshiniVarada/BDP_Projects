import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FaceBookMutualFriends {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text> {
        
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
        	//Declarations for mapper
        	
        	//To store all the data in the input file in an array seperated by tab
        	String[] totaldata = value.toString().split("\\t");
        	
        	//To store mutual friends of two friends on the mapper 
        	 Text outputValues = new Text();
        	 
        	//To store two people group on key side of the mapper 
        	 Text outputKeys = new Text();
        	 
        	 //To store the first data in the group 
        	  String[] array1 = new String[15]; 
        	  
        	  //To store the next data in the group 
              String[] array2 = new String[15];
              
              //To store the first person friends 
      		  String[] friends1 = new String[20];
      		  
      	    //To store the second person friends 
      		  String[] friends2 = new String[20];
      		  
      		  //To combine person 1 and person 2 names
              String keys = "";
              
          for(int i=0;i<totaldata.length;i++)
          {
        	  
          	for(int k=i+1;k<totaldata.length;k++)
          	{
          	
          		String mapperout = "";
          		
          		
          		//Store the part after the array (that is the list of friends each separated by , to the array1)
          		array1 = totaldata[i].split("->");
          		
          		
          	     //Store the name of the person to the keys
            		keys =  array1[0];
                    
            		// split all the friends and push them into an array
            		friends1 = array1[1].split(",");
            		for(int j=0;j<array1[1].split(",").length;j++)
                	{
            			if(mapperout == "")
            				mapperout= friends1[j];
            			else
            				mapperout = mapperout + ","+ friends1[j];
                	}
            	
          		
          		
            		//Store the part after the array (that is the list of friends of second person  each separated by , to the array1)
            		array2 = totaldata[k].split("->");
            		
            	     //Store the name of the second person concatenated to first person separated by -
            		keys = keys + "-" +  array2[0];
            		
            		// split all the friends and push them into an array
            		friends2 = array2[1].split(",");
            		
            		for(int j=0;j<array2[1].split(",").length;j++)
                	{
            			
            			mapperout = mapperout + ","+ friends2[j];
                	}
            	
            		
            	//Storing the final values of mapper	
            	outputValues.set(mapperout);
          		
          	    //Storing the final keys of mapper	
          		outputKeys.set(keys);
        		
        		//Final key-value on mapper side
        		context.write(outputKeys, outputValues);
          	}
          }

        }
    }

    public static class IntSumReducer
            extends Reducer<Text, Text, Text, Text> {
       
        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {
        	
        	
        	Text result = new Text();
        	String reducerout = "";
          
         for(Text value: values)
         {
        	 
            //Each value will be in the form "B,C,D".So separating each character
        	 
        	 String[] Array = value.toString().split(",");
        	 
        	 
        	  for(int i=0;i <Array.length;i++)
        		  {
        				for(int k=i+1;k<Array.length;k++)
        		        	{
        					
        					//Checking if any character in the array is repeated or not if it is append it to the result
        		        		if(Array[i].equals(Array[k]))
        		        		{
        		        			if(reducerout == "")
        		        			{
        		        				reducerout = Array[i];
        		        			}else
        		        				reducerout = reducerout + "," + Array[i];
        		        			break;
        		        		}
        		        		
        		        	}
        		  }
        	 result.set(reducerout);
        	 context.write(key, result);
         }          
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "FaceBookMutualFriends");
        job.setJarByClass(FaceBookMutualFriends.class);
        job.setMapperClass(TokenizerMapper.class);
       // job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}