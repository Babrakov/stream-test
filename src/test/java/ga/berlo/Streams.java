package ga.berlo;

import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

public class Streams {

    private List<Employee> emps = List.of(
            new Employee("Tom","Reddle",121,42,Position.CHIEF),
            new Employee("Bob","Hopkins",51,22,Position.MANAGER),
            new Employee("John","Smith",75,15,Position.WORKER),
            new Employee("Sam","Drake",1024,77,Position.WORKER),
            new Employee("Dan","Askin",28,54,Position.MANAGER),
            new Employee("Ron","Baley",76,17,Position.MANAGER),
            new Employee("Cat","Wrottle",41,61,Position.WORKER),
            new Employee("George","Gershvin",89,34,Position.WORKER)
    );


    private List<Department> deps = List.of(
            new Department(1,0,"Head"),
            new Department(2,1,"West"),
            new Department(3,1,"East"),
            new Department(4,2,"Germany"),
            new Department(5,2,"France"),
            new Department(6,3,"Japan"),
            new Department(7,3,"Korea")
    );

    @Test
    public void creation() throws IOException {
        Stream<String> lines = Files.lines(Paths.get("some.txt"));
        Stream<Path> list = Files.list(Paths.get("./"));
        Stream<Path> walk = Files.walk(Paths.get("./"), 3);

        IntStream intStream = IntStream.of(1, 2, 3, 4);
        DoubleStream doubleStream = DoubleStream.of(1.3, 4.5, 2.9);
        IntStream range = IntStream.range(13, 28);
        IntStream rangeClosed = IntStream.rangeClosed(13, 28);

        int[] ints = {1, 2, 3, 4};
        IntStream stream = Arrays.stream(ints);
        Stream<String> stringStream = Stream.of("1", "2", "3", "4");
        Stream<? extends Serializable> stream1 = Stream.of("1", 2, "3", "4");

        Stream<String> build = Stream.<String>builder()
                .add("Jack")
                .add("Jack")
                .build();

        Stream<Employee> stream2 = emps.stream();
        Stream<Employee> employeeStream = emps.parallelStream();

        Stream<Event> generate = Stream.generate(() ->
                new Event(UUID.randomUUID(), LocalDateTime.now(), "")
        );

        Stream<Integer> iterate = Stream.iterate(1950, val -> val + 3);

        Stream<String> concat = Stream.concat(stringStream, build);

    }

    @Test
    public void terminate(){
        Stream<Employee> stream = emps.stream();
        long count = stream.count();
        System.out.println("Count: "+count);

        emps.stream().forEach(employee -> System.out.println(employee));
        emps.forEach(employee-> System.out.println(employee.getAge()));

        emps.stream().forEachOrdered(employee -> System.out.println(employee));

        System.out.println();
        System.out.print("To list: ");
        System.out.println(emps.stream().collect(Collectors.toList()));

        System.out.println();
        System.out.print("To array: ");
        Object[] objects = emps.stream().toArray();
        System.out.println(objects);

        System.out.println();
        System.out.print("To map: ");
        Map<Integer, String> collect = emps.stream().collect(Collectors.toMap(
                Employee::getId,
                emp -> String.format("%s %s", emp.getFirstName(), emp.getLastName())
        ));
        System.out.println(collect);

        int asInt = IntStream.of(100, 200, 300, 400).reduce((left, right) -> left + right).getAsInt();
        System.out.println("Sum of stream: " + asInt);

        int asIntOrElse = IntStream.of(100, 200, 300, 400).reduce((left, right) -> left + right).orElse(0);
        System.out.println("Sum of stream or else: " + asIntOrElse);

        Optional<Department> reduce = deps.stream().reduce(this::reducer);
        System.out.println(reduce);

        IntStream.of(100,200,300,400).average();
        IntStream.of(100,200,300,400).max();
        IntStream.of(100,200,300,400).min();
        IntStream.of(100,200,300,400).summaryStatistics().getMin();

        emps.stream().max(Comparator.comparingInt(Employee::getAge));

        emps.stream().findAny();
        emps.stream().findFirst();

        emps.stream().noneMatch(emp -> emp.getAge()>90); // true
        emps.stream().anyMatch(emp -> emp.getPosition() == Position.CHIEF); //true
        emps.stream().allMatch(emp -> emp.getAge()>10); //true

    }

    public Department reducer(Department parent, Department child){
        if(child.getParent() == parent.getId()) {
            parent.getChild().add(child);
        } else {
            parent.getChild().forEach(subParent -> reducer(subParent,child));
        }
        return parent;
    }

    @Test
    public void transform(){
        LongStream longStream = IntStream.of(100, 200, 300, 400).mapToLong(Long::valueOf);
        System.out.println(longStream.max());
        Stream<Event> eventStream = IntStream.of(100, 200, 300, 400).mapToObj(value ->
                new Event(UUID.randomUUID(), LocalDateTime.of(value, 12, 01, 00, 00), ""));
        System.out.println(eventStream.collect(Collectors.toList()));

        IntStream distinct = IntStream.of(100, 200, 300, 400, 100, 200).distinct();
        System.out.print("Distinct: ");
        distinct.forEach(e-> System.out.print(e+" "));
//        distinct.forEach(System.out::println);
        System.out.println();

        Stream<Employee> employeeStream = emps.stream().filter(emp -> emp.getPosition() != Position.CHIEF);
        System.out.println(employeeStream.collect(Collectors.toList()));

        emps.stream().skip(2);
        emps.stream().limit(4);

        emps.stream()
                .skip(3)
                .limit(4);

        emps.stream().sorted((o1,o2) -> o1.getAge()-o2.getAge());
        emps.stream().sorted(Comparator.comparingInt(Employee::getAge));

        emps.stream()
                .sorted(Comparator.comparingInt(Employee::getAge))
                .peek(emp -> emp.setAge(18))
                .map(emp -> String.format("%s %s", emp.getFirstName(), emp.getLastName()));

        System.out.println();
        emps.stream().takeWhile(emp -> emp.getAge()>20).forEach(System.out::println);
        System.out.println();
        emps.stream().dropWhile(emp -> emp.getAge()>20).forEach(System.out::println);

        IntStream.of(100,200,300,400)
                .flatMap(val -> IntStream.of(val-50,val))
                .forEach(System.out::println);

    }

    private void print(Stream<Employee> stream) {
        stream
                .map(employee -> String.format(
                        "%4d | %-15s %-10s age %s %s",
                        employee.getId(),
                        employee.getLastName(),
                        employee.getFirstName(),
                        employee.getAge(),
                        employee.getPosition()
                ))
                .forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void real(){
        Stream<Employee> stream = emps.stream()
                .filter(employee -> employee.getAge()<=30 && employee.getPosition() != Position.WORKER)
                .sorted(Comparator.comparing(Employee::getLastName));
        print(stream);

        Stream<Employee> sorted = emps.stream()
                .filter(employee -> employee.getAge() > 40)
                .sorted(Comparator.comparing(Employee::getAge))
                .limit(2)
                ;
        print(sorted);

        IntSummaryStatistics intSummaryStatistics = emps.stream()
                .mapToInt(Employee::getAge)
                .summaryStatistics();
        System.out.println(intSummaryStatistics);
        System.out.println(intSummaryStatistics.getMax());
        System.out.println(intSummaryStatistics.getMin());
        System.out.println(intSummaryStatistics.getAverage());
    }

}
