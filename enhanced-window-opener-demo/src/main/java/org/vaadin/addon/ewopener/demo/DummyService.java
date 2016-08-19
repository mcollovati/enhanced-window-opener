package org.vaadin.addon.ewopener.demo;

import com.oblac.nomen.Nomen;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DummyService {

    private static Random random = new Random();
    private static final List<Person> data;

    static {
        data = IntStream.range(0, 20)
            .mapToObj(i -> new Person(name(), random.nextInt(80) + 20))
            .collect(Collectors.toList());
    }

    public static List<Person> data() {
        return Collections.unmodifiableList(data);
    }

    private static String name() {
        return Nomen.est().superb().separator(" ").space(" ").person().get();
    }


    @Data
    public static class Person {
        private final String name;
        private final int age;
    }
}
