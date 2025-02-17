package magicofconch.sora.security.jwt;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final UserInfoRepository userInfoRepository;
	private final JwtUtil jwtUtil;
	private final SecurityUtil securityUtil;

	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String REFRESH_TOKEN_HEADER = "Refresh-Token";

	/**
	 * reissue access-token with refresh-token
	 * todo : RTR 구현 / RTR 구현시 blackList도 구현 필요
	 * @return new access token and refresh token(RTR)
	 */
	public TokenDto reissue(String refreshToken){

		if(refreshToken == null){ new BusinessException(ResponseCode.NO_REFRESH_TOKEN); }

		try{
			jwtUtil.isExpired(refreshToken);
		} catch(JwtException e){
			new BusinessException(ResponseCode.NO_REFRESH_TOKEN);
		}


		UserInfo userInfo = userInfoRepository.findUserInfoByUuid(jwtUtil.getUUID(refreshToken))
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), userInfo.getRole());

		return new TokenDto(accessToken, refreshToken);
	}
}
