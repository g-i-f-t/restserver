package kr.ac.jejunu.giftrestserver.service;

import kr.ac.jejunu.giftrestserver.exception.GiftException;
import kr.ac.jejunu.giftrestserver.model.User;
import kr.ac.jejunu.giftrestserver.payload.ValidatePayload;
import kr.ac.jejunu.giftrestserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BankService bankService;
    private final UserRepository userRepository;

    public void addUser(User user) {
        userRepository.save(user);
    }

    public Map<String, Object> authUser(ValidatePayload validatePayload) throws GiftException {
        Optional<User> userOptional = userRepository.findByEmailAndPassword(validatePayload.getEmail(), validatePayload.getPassword());

        if(userOptional.isEmpty()) throw new GiftException("couldn't find account", 400);
        User user = userOptional.get();
        String accessToken = refreshUserToken(user);
        return new HashMap<>() {{
            put("access_token", accessToken);
            put("user_seq_no", user.getUserSeqId());
        }};
    }


    public Optional<User> getAccountFromUserSeqId(String userSeqId) {
        return userRepository.findByuserSeqId(userSeqId);
    }

    /**
     * 리프레시 수행
     * @param user
     * @return accessToken
     */
    public String refreshUserToken(User user) {
        Map<String, Object> response = bankService.updateToken(user.getRefreshToken(), user.getScope());
        String accessToken = (String) response.get("access_token");
        String refreshToken = (String) response.get("refresh_token");
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return accessToken;
    }
}
