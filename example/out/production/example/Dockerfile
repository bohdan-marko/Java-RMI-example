# Використовуємо офіційний образ Java
FROM openjdk:11

# Копіюємо наш Java код в контейнер
COPY . /usr/src/myapp

# Встановлюємо робочий каталог
WORKDIR /usr/src/myapp

# Компілюємо наш Java код
RUN javac Server.java

# Відкриваємо порт 1099
EXPOSE 1099

# Запускаємо наш сервер при запуску контейнера
CMD ["java", "Server"]
