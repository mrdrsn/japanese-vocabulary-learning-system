package com.example.japanesevocabularylearningsystem.model;

public class TrainingExercise {

    public enum Type { A, B, C, D }

    private final Type type;
    private final ExerciseTypeA exerciseA;
    private final ExerciseTypeB exerciseB;
    private final ExerciseTypeC exerciseC;
    private final ExerciseTypeD exerciseD;

    private TrainingExercise(Type type, ExerciseTypeA a, ExerciseTypeB b,
                             ExerciseTypeC c, ExerciseTypeD d) {
        this.type = type;
        this.exerciseA = a; this.exerciseB = b;
        this.exerciseC = c; this.exerciseD = d;
    }

    public static TrainingExercise ofA(ExerciseTypeA a) {
        return new TrainingExercise(Type.A, a, null, null, null);
    }
    public static TrainingExercise ofB(ExerciseTypeB b) {
        return new TrainingExercise(Type.B, null, b, null, null);
    }
    public static TrainingExercise ofC(ExerciseTypeC c) {
        return new TrainingExercise(Type.C, null, null, c, null);
    }
    public static TrainingExercise ofD(ExerciseTypeD d) {
        return new TrainingExercise(Type.D, null, null, null, d);
    }

    public Type getType() { return type; }
    public ExerciseTypeA getExerciseA() { return exerciseA; }
    public ExerciseTypeB getExerciseB() { return exerciseB; }
    public ExerciseTypeC getExerciseC() { return exerciseC; }
    public ExerciseTypeD getExerciseD() { return exerciseD; }
}