package com.github.heisdanielade.pamietampsa.service;

import com.github.heisdanielade.pamietampsa.dto.pet.PetCreateDto;
import com.github.heisdanielade.pamietampsa.dto.pet.PetResponseDto;
import com.github.heisdanielade.pamietampsa.entity.AppUser;
import com.github.heisdanielade.pamietampsa.entity.Pet;
import com.github.heisdanielade.pamietampsa.exception.auth.AccountNotFoundException;
import com.github.heisdanielade.pamietampsa.exception.pet.PetAlreadyExistsException;
import com.github.heisdanielade.pamietampsa.repository.AppUserRepository;
import com.github.heisdanielade.pamietampsa.repository.PetRepository;
import com.github.heisdanielade.pamietampsa.util.DtoMapper;
import com.github.heisdanielade.pamietampsa.util.EmailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final AppUserRepository appUserRepository;
    private final EmailSender emailSender;

    @CacheEvict(value = "pets", allEntries = true)
    public void addPetToUser(String userEmail, PetCreateDto input, String imageURL){
        Optional<Pet> existingPet = petRepository.findByName(input.getName());
        if(existingPet.isPresent()){
            throw new PetAlreadyExistsException();
        }
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(AccountNotFoundException::new);
    
        Pet pet = new Pet(input.getName(), imageURL, input.getSpecies(), input.getBreed(), input.getSex());
        pet.setOwner(user);
        petRepository.save(pet);
        emailSender.sendPetRegistrationConfirmationEmail(userEmail, pet.getName(), pet.getSpecies());
    }

    @Cacheable("pets")
    public List<PetResponseDto> getPetsForUser(String userEmail){
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(AccountNotFoundException::new);

        List<Pet> pets = petRepository.findByOwner(user);

        System.out.println(System.lineSeparator() + "==== [PetService] Fetching pets for user("+ user.getId() + ") from DB" + System.lineSeparator()); // For testing cache
        return pets.stream()
                .map(DtoMapper::toPetDto)
                .toList();
    }
}
