import java.util.function.Function;
import java.util.stream.Stream;

class Functle<T extends Movable<T>> {
    private final Function<T, T> path;
    private final Function<T, T> reversePath;

    private Functle(Function<T,T> path, Function<T,T> reversePath) {
        this.path = path;
        this.reversePath = reversePath;
    }

    public static <T extends Movable<T>> Functle<T> of() {
        return new Functle<T>(x -> x, x -> x);
    }

    public Functle<T> forward(int steps) {
        Function<T, T> forwardMove = t -> { t.moveForward(steps); return t; };
        Function<T, T> reverseMove = t -> { t.moveForward(-steps); return t; };

        return new Functle<T>(
                this.path.andThen(forwardMove),
                reverseMove.andThen(this.reversePath)
                );
    }

    public Functle<T> left(int theta) {
        Function<T,T> leftTurn = t -> { t.turnLeft(theta); return t; };
        Function<T,T> rightTurn = t -> { t.turnLeft(-theta); return t; };

        return new Functle<T>(
                this.path.andThen(leftTurn),
                rightTurn.andThen(this.reversePath)
       );
    }

    public Functle<T> backward(int steps) {
        Function<T, T> backwardMove = t -> { t.moveForward(-steps); return t; };
        Function<T, T> reverseMove = t -> { t.moveForward(steps); return t; };

        return new Functle<T>(
                this.path.andThen(backwardMove),
                reverseMove.andThen(this.reversePath)
                );
    }

    public Functle<T> right(int theta) {
        Function<T,T> leftTurn = t -> { t.turnLeft(theta); return t; };
        Function<T,T> rightTurn = t -> { t.turnLeft(-theta); return t; };

        return new Functle<T>(
                this.path.andThen(rightTurn),
                leftTurn.andThen(this.reversePath)
       );
    
    }

    public Functle<T> reverse() {
        return new Functle<T>(
                this.path.andThen(this.reversePath),
                x -> x
                );
    }

    public Functle<T> andThen(Functle<T> other) {
        return new Functle<T>(
                this.path.andThen(other.path),
                other.reversePath.andThen(this.reversePath)
                );
    }

    public Functle<T> loop(int n) {
        return Stream.generate(() -> this)
            .limit(n)
            .reduce(Functle.of(), (a,b) -> a.andThen(b));
    }

    public Functle<T> comeHome() {

        Function<T, T> homePath = start -> {
            int i = 1;

            while (true) {
                Functle<T> attempt = this.loop(i);

                attempt.run(start);

                if (start.equals(() -> attempt.reversePath.apply(start))) {
                    return start;
                }

                i++;
            }
        };

        return new Functle<T>(homePath, homePath);
    }


    public T run(T obj) {
        return this.path.apply(obj);
    }

}
