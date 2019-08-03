package team02.project.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Temporary solution until someone comes up with a better abstraction
 */
public class Parameters {
    @Setter @Getter private static int numProcessors;
    @Setter @Getter private static int numTasks;
}
