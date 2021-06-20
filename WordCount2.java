package wordcount2;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount2 {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    //Mapper Function
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    //Reducer Function
    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      // Counting the frequency of each words
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
	//create new configuration object
    Configuration conf = new Configuration();
    //create new job
    Job job = Job.getInstance(conf, "word count");
    //set class
    job.setJarByClass(WordCount2.class);
    //set mapper class
    job.setMapperClass(TokenizerMapper.class);
    //set combiner class
    job.setCombinerClass(IntSumReducer.class);
    //set reducer class
    job.setReducerClass(IntSumReducer.class);
    //set key class
    job.setOutputKeyClass(Text.class);
    //set value class
    job.setOutputValueClass(IntWritable.class);
    //add input path
    FileInputFormat.addInputPath(job, new Path(args[0]));
    //add output path 
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    //exit
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}