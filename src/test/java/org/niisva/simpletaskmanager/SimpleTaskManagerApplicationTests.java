package org.niisva.simpletaskmanager;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SimpleTaskManagerApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testTaskManager() throws Exception {
        checkThatTaskQueueIsEmpty();
        runNew("init");
        String task1 = taskTake("Task1");
        checkThatTaskQueueIsEmpty();
        taskResult("Task1", "initRes1", task1, "Res1");
        String task2 = taskTake("Task2");
        String task3 = taskTake("Task3");
        checkThatTaskQueueIsEmpty();
        taskResult("Task2", "initRes1Res2", task2, "Res2");
        String task4 = taskTake("Task4");
        taskResult("Task4", "initRes1Res2Res4", task4, "Res4");
        String task5 = taskTake("Task5");
        taskResult("Task5", "initRes1Res2Res5", task5, "Res5");

        taskResult("Task3", "initRes1Res3", task3, "Res3");
        String task6 = taskTake("Task6");
        String task7 = taskTake("Task7");
        taskResult("Task7", "initRes1Res3Res7", task7, "Res7");
        taskResult("Task6", "initRes1Res3Res6", task6, "Res6");
    }
    private void checkThatTaskQueueIsEmpty() throws Exception {
        mvc.perform(post("/simple-task-manager/task-take"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
    private void runNew(String startValue) throws Exception {
        mvc.perform(post("/simple-task-manager/run-new")
            .param("startValue", startValue).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
    private String taskTake(String expectedTaskName) throws Exception {
        String json = mvc.perform(post("/simple-task-manager/task-take")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.taskName").value(expectedTaskName))
                .andReturn().getResponse().getContentAsString();
        return new JSONObject(json).getString("taskID");
    }
    private void taskResult(String expectedTaskName, String expectedResult, String taskID, String result) throws Exception {
        mvc.perform(post("/simple-task-manager/task-result")
                .param("taskID", taskID)
                .param("result", result)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.taskName").value(expectedTaskName))
                .andExpect(jsonPath("$.taskResult").value(expectedResult));
    }
}
