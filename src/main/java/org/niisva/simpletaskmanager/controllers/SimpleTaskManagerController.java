package org.niisva.simpletaskmanager.controllers;

import org.niisva.simpletaskmanager.models.Task;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@RestController
@RequestMapping("/simple-task-manager")
public class SimpleTaskManagerController {
    private BlockingQueue<Task> tasksWaiting = new LinkedBlockingQueue<>();
    private ConcurrentMap<String, Task> tasksInProgress = new ConcurrentHashMap<>();

    /**
     * Запускает процесс выполнения новой иерархии задач
     * @param startValue начальное значение для Task1
     */
    @PostMapping(value = "/run-new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String runNew(@RequestParam(value = "startValue") String startValue) throws Task.TaskBuilderException {
        Task newTask = new Task.Builder("Task1", startValue)
                .addTask("Task2")
                    .addTask("Task4").up()
                    .addTask("Task5").up()
                    .up()
                .addTask("Task3")
                    .addTask("Task6").up()
                    .addTask("Task7").up()
                .build();
        tasksWaiting.add(newTask);
        return "{\"success\": true}";
    }

    /**
     * Взятие задачи на исполнение воркером (startValue задачи установлено)
     * @return taskID и startValue задачи
     */
    @PostMapping(value = "/task-take", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String taskTake() throws Exception {
        Task task = tasksWaiting.poll();
        if(task == null)
            throw new Exception("Очередь задач пуста");
        tasksInProgress.put(task.getID(), task);
        return String.format("{\"success\": true, \"taskID\": \"%s\", \"startValue\": \"%s\", \"taskName\": \"%s\"}",
            task.getID(), task.getStartValue(), task.getName());
    }

    /**
     * Уведомление о завершении задания воркером
     * @param taskID идентификатор задачи
     * @param result результат выполнения задачи
     * @return результат выполнения задачи (c учётом startValue)
     */
    @PostMapping(value = "/task-result", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String taskResult(@RequestParam(value = "taskID") String taskID,
        @RequestParam(value = "result") String result) throws Exception
    {
        Task task = tasksInProgress.get(taskID);
        if(task == null)
            throw new Exception("Задача с таким taskID не найдена в списке запущенных");
        tasksInProgress.remove(taskID);
        String taskResult = task.getStartValue() + result;
        List<Task> tasksReadyToRun = task.getDependentTasks();
        tasksReadyToRun.forEach(t -> t.setStartValue(taskResult));
        tasksWaiting.addAll(tasksReadyToRun);

        return String.format("{\"success\": true, \"taskResult\": \"%s\", \"taskName\": \"%s\"}",
            taskResult, task.getName());
    }

}
