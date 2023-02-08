package doan.middle_project.controller;

import doan.middle_project.common.logging.LogUtils;
import doan.middle_project.common.utils.JwtResponse;
import doan.middle_project.common.utils.JwtUtils;
import doan.middle_project.common.utils.LoginRequest;
import doan.middle_project.common.utils.SignupRequest;
import doan.middle_project.common.vo.MessageVo;
import doan.middle_project.common.vo.UserDetailsImpl;
import doan.middle_project.repositories.AccountRepository;
import doan.middle_project.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthenController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AccountRepository accountRepository;


	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
		LogUtils.getLog().info("START login");
		if(loginRequest.getUsername() == null || loginRequest.getUsername() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập tên người dùng","error"));
		}
		if(loginRequest.getPassword() == null || loginRequest.getPassword() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập mật khẩu","error"));
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		Account account = accountRepository.findAccountByUserName(userDetails.getUsername());
		LogUtils.getLog().info("END login");
		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				roles, account.getAvatarImage()));
		}

	@PostMapping("/signout")
	public  ResponseEntity<?> logout(){
		ResponseCookie cookie  = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).
				body(new MessageVo("Bạn đã đăng xuất", "info"));
		}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		LogUtils.getLog().info("START registerUser");
		if(signUpRequest.getUsername() == null || signUpRequest.getUsername() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập tên người dùng","error"));
		}
		if(signUpRequest.getPassword() == null || signUpRequest.getPassword() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập mật khẩu","error"));
		}
		if(signUpRequest.getEmail() == null || signUpRequest.getEmail() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập email","error"));
		}
		if(signUpRequest.getFullname() == null || signUpRequest.getFullname() == ""){
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Chưa nhập họ tên của bạn","error"));
		}
		if (accountRepository.existsByUserName(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageVo( "Tên người dùng đã tồn tại","error"));
		}

		if (accountRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageVo("Email đã tồn tại", "error"));
		}

		// Create new user's account
		Account account = new Account(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()),
				signUpRequest.getEmail(), signUpRequest.getFullname());


		account.setRole("ROLE_USER");
		account.setStatus(1);
		accountRepository.save(account);
		LogUtils.getLog().info("END registerUser");
		return ResponseEntity.ok(new MessageVo("Bạn đã đăng ký thành công", "info"));
	}

}
