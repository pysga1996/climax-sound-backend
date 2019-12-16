package com.lambda.filters;

public class JwtAuthenticationFilter {
//        extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtTokenProvider tokenProvider;
//
//    @Autowired
//    private UserDetailServiceImpl userDetailService;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            // Retrieve jwt from request
//            String jwt = getJwtFromRequest(request);
//            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//                // Lấy username từ chuỗi jwt
//                String username = tokenProvider.getUsernameFromJWT(jwt);
//                // Lấy thông tin người dùng từ username
//                UserDetails userDetails = userDetailService.loadUserByUsername(username);
//                if(userDetails != null) {
//                    // Nếu người dùng hợp lệ, set thông tin cho Seturity Context
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//        } catch (Exception ex) {
//            logger.error("failed on set user authentication", ex);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        // Kiểm tra xem header Authorization có chứa thông tin jwt không
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
}
