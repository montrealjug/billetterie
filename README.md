# Billetterie

A webapp to manage the ticketing for a Devoxx4Kids event

## See it in action

https://billetterie.fly.dev/


## Run the app in development

Make sure to have a Docker daemon running (so that tests and dev. can start the postgresql container).

And then:

`./mvnw spring-boot:run`

### Using development profiles

To ease local development, two profiles are available:
- `local`: for day to day development
- `local-mail`: to test sent emails locally (it's a group that loads `local` and `mailhog` profiles in the expected order) (see [email module documentation](./src/main/java/org/montrealjug/billetterie/email/README.md#how-to-test-locally) for more)

You can set your desired profile on the command line:

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
``` 
or 
```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local-mail
```
This can also be done in your `IDE` configuration.

### Issues you could encounter

#### Spotless format

If you have a failure during compilation, make sure to run:

```shell
./mvnw spotless:apply
```

to make sure your files are formatted according to our convention.

#### Database schema changes

If the database schema changed, because of JPA entities, the safest way to rebuild everything is... to delete your DB!

Simply issue this command:

```shell
docker-compose down
```

Notes for administrators connecting to the Fly.io db: you can't simply destroy it. 

Here are commands that could help:

```shell
# open a postgres shell
fly postgres connect -a billetterie-db
# list databases
\l
# drop the database altogether
DROP DATABASE billetterie;
# if that fails because of existing connections, list them and kill them
select pid,state from pg_stat_activity;
select pg_terminate_backend(`PID`);
# re create the DB
CREATE DATABASE billetterie;
# Make sure user billetterie can interact with its database
GRANT ALL PRIVILEGES ON DATABASE "billetterie" to billetterie;
```
