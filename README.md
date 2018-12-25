# Learning management system

[![codecov](https://codecov.io/gh/egdeliya/Learning-management-system/branch/master/graph/badge.svg)](https://codecov.io/gh/egdeliya/Learning-management-system)
[![Build Status](https://travis-ci.org/egdeliya/Learning-management-system.svg?branch=master)](https://travis-ci.org/egdeliya/Learning-management-system)

LMS (англ. _learning management system_) — [система управления обучением].

[система управления обучением]: https://ru.wikipedia.org/wiki/%D0%A1%D0%B8%D1%81%D1%82%D0%B5%D0%BC%D0%B0_%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F_%D0%BE%D0%B1%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5%D0%BC

Приложение для управления процессом обучения. Предполагается, что преподаватели и студенты посредством интерфейса приложения
смогут осуществлять процесс обучения :) 

На данный момент реализован функционал для администратора. Пользователи могут зарегистрироваться в системе.

### Административная часть

Администратор напрямую взаимодействует с базой данных.
Он может 
* создавать учебные курсы
* записывать группу на курс
* добавлять преподавателя курса
* осуществлять предварительную регистрацию пользователей

[`Пример действий администратора`](https://github.com/egdeliya/Learning-management-system/blob/master/src/main/scala/Admin/AdminExample.scala) 

Здесь приведен пример инициализации сервисов и заполнения базы данных тестовыми данными.

### Публичное API

Пользователи могут

* зарегистрироваться в системе, если есть токен

 `/register`
```json
    {
      "email": "some@email",
      "token": "from admin",
      "password": "secure password"
    }
```
    
`POST`  Ответ:  

```HTTP/1.1 201 OK```
* войти в систему со свом логином и паролем

 `/login`
```json
    {
      "email": "some@email",
      "password": "secure password"
    }
```
    
`POST`  Ответ:  

```HTTP/1.1 200 OK```

### Зависимости 

[`sbt`](https://www.scala-sbt.org/1.0/docs/Setup.html)

[`build.sbt`](https://github.com/egdeliya/Learning-management-system/blob/master/build.sbt) +  [`project/Dependencies.scala`](https://github.com/egdeliya/Learning-management-system/blob/master/project/Dependencies.scala)

### Запуск приложения
 
 Заполнение базы тестовыми данными. Будут выведены примеры сгенерированных токенов, которые можно
 использовать при регистрации. 
 
 ```sbt "runMain Admin.AdminExample" ```
 
 Запуск приложения
 
 ```sbt "runMain Api.ApplicationApp"```
 
 Приложение запустится на [`http://localhost:8080`](http://localhost:8080)
 
 ### Запуск тестов
 
 ```sbt test```
 
 ### Статический анализ стиля кодирования
 
``` sbt scalastyle ```

### Непрерывная интеграция

[`Travis`](https://travis-ci.org/egdeliya/Learning-management-system)

### Измерение покрытия кода

[`CodeCov`](https://codecov.io/gh/egdeliya/Learning-management-system/branch/master)

### Реляционная СУБД 

[`H2`](http://www.h2database.com/html/main.html)


[license]: LICENSE