/*
 * Copyright (C) 2016-2017 Marco Collovati (mcollovati@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
