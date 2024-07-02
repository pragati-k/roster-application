package com.rostering.employeerostering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rostering.employeerostering.dto.EmployeePairDTO;
import com.rostering.employeerostering.entity.ScheduleModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ScheduleModelRotation {

    public List<Integer> repeatTillNNonConsecutive(List<Integer> numbers, long n) {
        List<Integer> result = new ArrayList<>((int) n);

        // Check if the numbers list is empty or n is zero or less, return an empty list
        if (numbers.isEmpty() || n <= 0) {
            return result;
        }
        if(numbers.size() == 1){
            for(int i =0; i < n; i++){
                result.addAll(numbers);
            }
            return result;
        }


        int index = 0;
        int previousNumber = -1; // Assuming all numbers are positive, otherwise use a different sentinel

        for (int i = 0; i < n; i++) {
            // Find the next number that is not the same as the previous number
            while (numbers.get(index) == previousNumber) {
                index = (index + 1) % numbers.size();
            }

            // Add the selected number to the result
            result.add(numbers.get(index));
            previousNumber = numbers.get(index);

            // Move to the next number in the list
            index = (index + 1) % numbers.size();
        }

        return result;
    }


    public static List<ScheduleModel> getScheduleModelList() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ClassPathResource scheduleModelsResource = new ClassPathResource("data/ScheduleModels.json");
        List<ScheduleModel> scheduleModels = Arrays.asList(objectMapper.readValue(scheduleModelsResource.getInputStream(), ScheduleModel[].class));

        return scheduleModels;
    }

    public static List<EmployeePairDTO> getEmployeePair() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ClassPathResource scheduleModelsResource = new ClassPathResource("data/EmployeePairs.json");
        List<EmployeePairDTO> employeePairDTOList = Arrays.asList(objectMapper.readValue(scheduleModelsResource.getInputStream(), EmployeePairDTO[].class));

        return employeePairDTOList;
    }
}
