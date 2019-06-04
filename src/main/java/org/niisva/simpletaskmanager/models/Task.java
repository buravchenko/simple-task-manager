package org.niisva.simpletaskmanager.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private Task() { }

    private String id;
    private String name;
    private String startValue;
    private List<Task> dependentTasks;
    private Task parent;

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public List<Task> getDependentTasks() {
        return dependentTasks;
    }

    public static class Builder {
        private Task rootTask;
        private Task currentTask;

        public Builder(String taskName, String startValue) {
            Objects.requireNonNull(taskName);
            Objects.requireNonNull(startValue);
            rootTask = new Task();
            rootTask.id = UUID.randomUUID().toString();
            rootTask.name = taskName;
            rootTask.startValue = startValue;
            rootTask.dependentTasks = new ArrayList<>();
            rootTask.parent = null;
            currentTask = rootTask;
        }

        public Builder addTask(String taskName) {
            Objects.requireNonNull(taskName);
            Task newTask = new Task();
            newTask.id = UUID.randomUUID().toString();
            newTask.name = taskName;
            newTask.startValue = null;
            newTask.dependentTasks = new ArrayList<>();
            newTask.parent = currentTask;
            currentTask.dependentTasks.add(newTask);
            currentTask = newTask;
            return this;
        }

        public Builder up() throws TaskBuilderException {
            currentTask = currentTask.parent;
            if(currentTask == null)
                throw new TaskBuilderException("Метод up применён к корневой задаче");
            return this;
        }

        public Task build() {
            return rootTask;
        }
    }

    public static class TaskBuilderException extends Exception {
        public TaskBuilderException(String message) {
            super(message);
        }
    }
}
