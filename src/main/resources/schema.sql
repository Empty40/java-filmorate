drop table IF EXISTS USERS, FRIENDSHIP, FILMS, FILM, GENRES, MPA, FILM_LIKES, FILM_GENRES, FILM_MPA, DIRECTORS,
    FILM_DIRECTOR, REVIEWS, EVENT_FEED CASCADE;

create TABLE IF NOT EXISTS USERS
(
    user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(255) UNIQUE,
    login varchar(255) UNIQUE,
    name varchar(255),
    birthday DATE
);

create TABLE IF NOT EXISTS MPA
(
    mpa_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(255)
);

create TABLE IF NOT EXISTS GENRES
(
    genre_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255)
);

create TABLE IF NOT EXISTS FILMS
(
    film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255),
    description varchar(255),
    releaseDate DATE,
    duration integer,
    mpa_id integer REFERENCES MPA (mpa_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS FILM_LIKES
(
    film_id integer REFERENCES FILMS (film_id) ON delete CASCADE,
    user_id integer REFERENCES USERS (user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS FILM_MPA
(
    film_id integer REFERENCES FILMS (film_id) ON delete CASCADE,
    mpa_id integer REFERENCES MPA (mpa_id)
);

create TABLE IF NOT EXISTS FILM_GENRES
(
    film_id integer REFERENCES FILMS (film_id) ON delete CASCADE,
    genre_id integer REFERENCES GENRES (genre_id)
);

create TABLE IF NOT EXISTS FRIENDSHIP
(
    user_id integer REFERENCES USERS (user_id) ON delete CASCADE,
    friend_id integer REFERENCES USERS (user_id) ON delete CASCADE,
    friends boolean
);

create table IF NOT EXISTS Directors(
director_id int generated by default as identity primary key,
director_name varchar(64) not null
);
create unique index IF NOT EXISTS director_exists
ON Directors(director_name);

create table IF NOT EXISTS Film_director(
  film_id int NOT NULL,
  director_id int NOT NULL,
      FOREIGN KEY(film_id)
      REFERENCES Films(film_id) ON delete CASCADE,
      FOREIGN KEY(director_id)
      REFERENCES Directors(director_id) ON delete CASCADE
);
create unique index IF NOT EXISTS film_director_exists
ON Film_director(film_id, director_id);

CREATE TABLE IF NOT EXISTS REVIEWS
(
    review_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content varchar(255),
    isPositive boolean,
    user_id integer REFERENCES USERS (user_id) ON delete CASCADE,
    film_id integer REFERENCES FILMS (film_id) ON delete CASCADE,
    useful integer
    );

CREATE TABLE IF NOT EXISTS EVENT_FEED
(
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_timestamp TIMESTAMP NOT NULL,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    event_type VARCHAR(255) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    entity_id INTEGER NOT NULL
);

