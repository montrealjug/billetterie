# Billetterie

A webapp to manage the ticketing for a Devoxx4Kids event

## See it in action

https://billetterie.fly.dev/


## Run the app in development

Make sure to have a Docker daemon running (so that tests and dev. can start the postgresql and mailhog containers).

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

### Tailwind build integration

We use `tailwind` for our `css`.  

***Tailwind build is disabled for now, to speed dev.***

***To enable it:***
- ***remove the `skip` tag in the `frontend-maven-plugin` in the `pom.xml`***
- ***replace the tags in the `admin_layout.jte` and `guest_layout.jte` by the commented one***


The generation of the `main.css` file is handled by the `frontend-maven-plugin` during the build.

As explained [in the documentation of the plugin](https://github.com/eirslett/frontend-maven-plugin/blob/master/README.md#what-is-this-plugin-not-meant-to-do), it is focused on the build process, not on the `dev` experience. Especially, it is not possible to use it to launch tasks in `watch` mode (maven will simply stop the process).
To run `tailwind` in `watch` mode during dev, you have to use a dedicated `shell` and launch, from the project root directory:
```shell
npm --prefix src/main/tailwind run watch
```

Coupled with `spring-boot-dev-tools`, and our `local` profile this will update generated `css` on any changes in the templates, making the changes visible in the browser on refresh. 

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
# or  docker-compose --profile mailhog down
```

## Run the app in production (fly.io)

### Database schema changes

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

### Configure the SMTP Server in production

Set those environment variables:

* MAIL_SERVER , defaults to in-v3.mailjet.com
* MAIL_USER
* MAIL_PASSWORD

### Configure a non default RSA Key

The RSA key is used to create signatures of the email addresses of the booker; this signature is then used to identify and manage the bookings of the user (booker).

```bash
# Generate a 2048-bit RSA private key in PKCS#8 format
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
```

then set the env. variable / secret `APP_RSA_KEY`

### Configure security

Set at least those environment variables:
* `ACTUATOR_PASSWORD`, defaults to `actuator` 😱😉
* `ADMIN_PASSWORD`, defaults to `password` 😱😉

You can also set:
* `ADMIN_USER`, defaults to `admin` to... set the `admin` username
* `ADMIN_SESSION_COOKIE_NAME`, defaults to `BILLETTERIE_SESSION` to ... set the `admin` session cookie name
