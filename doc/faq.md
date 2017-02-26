##FAQ

**Q: How to update the database model with JHipster?**  
**A:**

1. Modify your entities as you need (with the entity sub-generator or by editing the JPA entities manually)

2. If you have used the entity sub-generator, you don't have to overwrite the changelog and the html views.

3. Run the following command to compile the application and generate a changelog in the *src/main/resources/config/liquibase/changelog* directory
 ```bash
 ./mvnw compile liquibase:diff
 ```

4. Add the new changelog in the *src/main/resources/config/liquibase/master.xml* file at the end of the file.

5. That's done ! You can restart your application `./mvnw` and the changes will be take into account in the database.  

**Q: How to easily import data in CSV format in the application?**  
**A:**

We develop an other project to do it.  
[Link to the import-toast-data project](https://github.com/Mariam112/import-toast-data)
