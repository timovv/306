package team02.project.graph;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.HashSet;
import java.util.Set;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {

    private static int counter = 0;

    /** This is illegal, but lombok is cancer so its ok **/
    @NonFinal @EqualsAndHashCode.Include int id;
    { this.id = counter++; }

    String name;
    int duration;
    Set<Dependency> children = new HashSet<>();
    Set<Dependency> parents = new HashSet<>();

}
