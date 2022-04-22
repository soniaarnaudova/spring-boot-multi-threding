package com.soar.springboot.multithreading.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.soar.springboot.multithreading.entity.User;
import com.soar.springboot.multithreading.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    Object target;

    Logger logger= LoggerFactory.getLogger(UserService.class);

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception{
        long start = System.currentTimeMillis();
        List<User> users = this.parseCSVFile(file);
        logger.info("Saving list of users of size {}", users.size()," "+Thread.currentThread().getName());
        users = repository.saveAll(users);
        long stop = System.currentTimeMillis();
        logger.info("Total time {}",(stop-start));

        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers(){
        logger.info("Get list of users by "+ Thread.currentThread().getName());
        List<User> users = repository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(MultipartFile file) throws Exception{
        logger.info("Parsing CSV file: "+file.getOriginalFilename());
        List<User> users = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // create csv bean reader
         /*   CsvToBean<User> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(User.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // convert `CsvToBean` object to list of users
            users = csvToBean.parse();*/
         String line;
         while((line = ((BufferedReader) reader).readLine()) != null){
             String[] data = line.split(",");
             User user = new User();
             user.setName(data[0]);
             user.setEmail(data[1]);
             user.setGender(data[2]);
             users.add(user);
         }
        }catch(final IOException e){
            logger.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
        logger.info("End of parsing. "+users.size());
        return users;
    }


}
