package com.model.apn;
/*
import com.model.apn.DataStructure.Instance;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class tmp {
    void aaa(String str[]){
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(toList());
        System.out.println(filtered);

        List a= new ArrayList<String>();
        a.addAll(Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl"));
        a.stream().forEach(aa->{
            System.out.print(aa+" ");
        });

        Stream va=a.stream();
        va.forEach(aa->{
            System.out.print(aa+" ");
        });
        System.out.println();
        System.out.println("AA".hashCode());
        System.out.println("AA".hashCode());

        ArrayList<String> aaa = new ArrayList<> ();
        ArrayList bbb = aaa;
        HashArrayList<String> ccc = new HashArrayList<> ();
        HashArrayList<String> ccc2 = new HashArrayList<> ();
        aaa.add("AA");
        bbb.add("NN");
        ccc.add("AA");
        ccc.add("NN");

        ccc2.add("NN");

        System.out.println(ccc.hashCode()+" "+ccc.toString());
        System.out.println(ccc2.hashCode()+" "+ccc2.toString());

        ccc.equals(ccc2);

        BitSet xas = new BitSet();
        xas.set('a');
        BitSet sda = new BitSet();
        sda.set(97);
        sda.set(97);
        sda.set(97);
        sda.set(97);
        sda.set(97);
        sda.set(98);
        System.out.println(xas.get('a')+" "+xas.hashCode()+" "+xas.size());
        System.out.println(sda.get(97)+" "+sda.hashCode()+" "+sda.size()+" "+sda.toString()+" "+sda);


        BitSet bba=new BitSet(1024);

        int foo = Integer.parseInt("1234566");
        System.out.println(foo);
        //bba.set(ia);
        //byte[] byteb = bba.toByteArray();
        //System.out.println(new String(byteb));
        byte[] byteb = "1".getBytes();

        for(int i=0;i<100;i++){

            bba.set(i,true);
        }

        System.out.println(new String(byteb)+" "+byteb.length+" "+ ObjectSizeCalculator.getObjectSize(bba));

        String as= IntStream
                .range(0, 13)
                .mapToObj(i -> bba.get(i) ? '1' : '0')
                .collect(
                        () -> new StringBuilder(13),
                        (buffer, characterToAdd) -> buffer.append(characterToAdd),
                        StringBuilder::append
                )
                .toString();

        System.out.println(as);


        //Get the file reference
        Path inputpath = Paths.get("C:/Data/Iris", "origin.txt");
        Charset charset = Charset.forName("UTF-8");


        //Use try-with-resource to get auto-closeable writer instance
        try {
            List<Instance> str2= Files.newBufferedReader(inputpath, charset).lines().map(mapToItem).collect(Collectors.toList());
            System.out.println(str2.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


        HashMap ssa=new HashMap(1);
        ssa.put(aaa.hashCode(),aaa);
        System.out.println(ssa.size());
        ssa.put(aaa,aaa);
        System.out.println(ssa.size());


        Random random = new Random();
        random.ints().limit(10).forEach(System.out::println);

        List<String>strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());

        System.out.println("筛选列表: " + filtered);
        String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(", "));
        System.out.println("合并字符串: " + mergedString);

    }

    private static Function<String, Instance> mapToItem = (line) -> {

        String[] p = line.split(",");// a CSV has comma separated lines
        Instance item = new Instance();
        //more initialization goes here
        return item;
    };
}
*/