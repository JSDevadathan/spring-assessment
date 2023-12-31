package com.example.movierental.service;

import com.example.movierental.contract.MovieResponseDto;
import com.example.movierental.expection.MovieAlreadyExistsException;
import com.example.movierental.expection.MovieNotFoundException;
import com.example.movierental.model.Movie;
import com.example.movierental.repository.MovieRepository;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    public List<MovieResponseDto> getAllMovies() {
        List<Movie> movies = this.movieRepository.findAll();
        List<MovieResponseDto> responses = new ArrayList<>();
        for (Movie m : movies) {
            responses.add(modelMapper.map(m, MovieResponseDto.class));
        }
        return responses;
    }

    public MovieResponseDto getMovieById(int id) throws MovieNotFoundException {
        Movie movie =
                this.movieRepository
                        .findById((long) id)
                        .orElseThrow(
                                () -> {
                                    log.error("Movie with id: {} not found", id);
                                    return new MovieNotFoundException(id);
                                });
        return modelMapper.map(movie, MovieResponseDto.class);
    }
    public List<MovieResponseDto> getMoviesByYear(int year) {
        Year releaseYear = Year.of(year);
        List<Movie> movies = movieRepository.findByReleaseYear(releaseYear);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieResponseDto.class))
                .collect(Collectors.toList());
    }
    public List<MovieResponseDto> getMoviesByStatus(String status) {
        List<Movie> movies = movieRepository.findByStatus(status);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieResponseDto.class))
                .collect(Collectors.toList());
    }

    public MovieResponseDto addMovie(MovieResponseDto movie) {
        boolean movieExists = movieRepository.existsMovieByTitle(movie.getTitle());
        if (movieExists) {
            throw new MovieAlreadyExistsException(movie.getTitle());
        }
        Movie savedMovie = movieRepository.save(modelMapper.map(movie, Movie.class));
        return modelMapper.map(savedMovie, MovieResponseDto.class);
    }

    public MovieResponseDto updateMovieById(int id, Movie movie) throws MovieNotFoundException {
        Movie existingMovie =
                movieRepository
                        .findById((long) id)
                        .orElseThrow(
                                () -> {
                                    log.error("Movie with id: {} not found", id);
                                    return new MovieNotFoundException(id);
                                });

        modelMapper.map(movie, existingMovie);
        Movie updatedMovie = movieRepository.save(existingMovie);
        return modelMapper.map(updatedMovie, MovieResponseDto.class);
    }

    public void deleteMovieById(int id) throws MovieNotFoundException {
        if (!movieRepository.existsById((long) id)) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.deleteById((long) id);
    }
}
