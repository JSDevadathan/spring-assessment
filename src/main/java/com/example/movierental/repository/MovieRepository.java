package com.example.movierental.repository;

import com.example.movierental.model.Movie;

import java.time.Year;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsMovieByTitle(String title);
    List<Movie> findByStatus(String status);
    List<Movie> findByReleaseYear(Year releaseYear);
}