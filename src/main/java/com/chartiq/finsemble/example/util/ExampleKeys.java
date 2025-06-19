package com.chartiq.finsemble.example.util;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

/**
 * Example keys for static auth.
 */
public class ExampleKeys {

    // The logger
    private final static Logger LOGGER = Logger.getLogger(ExampleKeys.class.getName());

    // The private key instance
    private static RSAPrivateKey KEY_RSA_PRIVATE;

    // The JSON for the example key (see notes at the end of this file)
    private final static String KEY_JSON_PRIVATE = """
            {
                "alg": "RS256",
                "d": "D8W0Qk2kySn2XPwFCl7oJBVwreeE0lF7eJnHa1nmItrITzba4ktKsdixq-fYprLs6_bo4Ietf39i_cGSwK_Ob3K9-8COBaL5SitgDtikPhGDD1KMwKYr-1RIWGVFE74X-G2Sp94tHTtNJqaxUTPbxHfyMiOKqBlFvYCzS1Zrbpt6DU7vSN25ZBSszgTZoZgm7GQ20ZQzul_sV6VczYlEeu_NHilH67jaUNOaH38s5E7q-VsSyStEOu90SONlnVpSBOaVnGNBqDUtZTnicQpqnvaPMZHTD1hahbncLhanuqglLpJ7xztPjg8CifJ5zqyC7CAaETj8zbdcoZE8_AdUwQ",
                "dp": "aXk30r_al9qS_bCMit8VW8MX2VDZdkA37nZY2q6MlaORbOSsh7UT6x2pEofJitdr28KKQl4QFmHPrQu9-d8Kb9dcUs8mTl2857B78ujTnhVmq3tbktmE4Y9BhdJHAqwwgT4XZG1ETdVRj1c0A8czYQfwljvwA3EeOTo_rJMwjIE",
                "dq": "MF5nH7a7IsKIUlbEbjDIjJt8hc4iwchNed6Kmy84n28FdEhFtkMJjI8wb0OEQJPgDP6Hx8TRj00qz1PArPC7W21cfB41VVCJcNpY8LK7oeiNr7GJ0dCwjZ7F_ViDOmG06RXcgZF5FDSO_n-QqIw6EFb0cVntGKWXtqL1WYW1Vjk",
                "e": "AQAB",
                "ext": true,
                "key_ops": [
                    "sign"
                ],
                "kty": "RSA",
                "n": "x7HC2OMop_3mQVOSE8FdHTIf_aLJDtbz8vSwEnaMBjo-Sl2__FlRcxetooceTop8DnuCmYQjH3YKRPIdkC5yIkHsHj6gLMIQc5BJ5jGJBIaHR83ayzBc2tvX7JgNmee6MWjKWVXLRk-R6Dp0yWVr97oAkqzqQiUPTt45MtCfGnlePTj5XT1otLQ478zgpzBxtk1VaYOhzGEXIrWKG2jKO-CydbsJA5Az4NejqFSpexWk7U6Fy8cjN0B5s_tFlg_GlmHE89_N2SauBmCvcX_Pn4s38ZsxvR9Q2i_I4I4eocZ5ujAq9b0qTnrORHxwLPMV6YANqpz8C6Bnt46o2CerzQ",
                "p": "-CFjVnhItE7Vv9ELQs3PLMsoMs5GFsS_fwmkhOkvXTzYERyXoS32VZ-fZvK_3QPwhHXmpTq-Qz9e_XoLoVufK2i-NHxAc09MYKYty3GDWBl66HA_8TJWX1niA_fPhm84v7dMf3IMRNNaHi9pZM0Z3HxUc-b9EnHH5G-a57YETEE",
                "q": "zgcc4unaBcBb7PNNDV1BJW_t6zZ72wwAK1OzF5-xcaKSrGdTM4789w-OkPQtuWNoq6-MkSoNC-zeGVVJ_H79cLiejmdh7SE5F9sorpDTuA7mvzJfj5DC146m62BpvGWMtVbYn7ItBLgkQZko90NL2QQsMmu5oPBmKgo_xEkOrI0",
                "qi": "Tef18Zw3xJa9HC9eWJNAJB00Sire-FPRrpSxPvUSTyaTSisLr62hUXvWj0j_0SLjLtYdMxkhHGwUez35qC-rLsP5KUTBV9r2-rP7N0gWqbwyBhDAYh7o5WhCfM4TnJmz_nF_uBD_76yD7kGD-eDLqq_p85tOXJBnOHv0_3xmRic"
            }
            """;

    // Parse the key when this class is loaded
    static {
        try {
            KEY_RSA_PRIVATE = RSAKey.parse(KEY_JSON_PRIVATE).toRSAPrivateKey();
        } catch (final Exception e) {
            LOGGER.warning("Could not parse the private key; static auth cannot be used");
        }
    }

    /**
     * Get the private key instance for this example app.
     *
     * @return the private key instance for this example app
     */
    public static RSAPrivateKey getPrivateRSAKey() {
        return KEY_RSA_PRIVATE;
    }

    /**
     * Private constructor for singleton
     */
    private ExampleKeys() {}

}

//<editor-fold defaultstate="collapsed" desc="JWT">
// Private key:
// {
//   "alg": "RS256",
//   "d": "D8W0Qk2kySn2XPwFCl7oJBVwreeE0lF7eJnHa1nmItrITzba4ktKsdixq-fYprLs6_bo4Ietf39i_cGSwK_Ob3K9-8COBaL5SitgDtikPhGDD1KMwKYr-1RIWGVFE74X-G2Sp94tHTtNJqaxUTPbxHfyMiOKqBlFvYCzS1Zrbpt6DU7vSN25ZBSszgTZoZgm7GQ20ZQzul_sV6VczYlEeu_NHilH67jaUNOaH38s5E7q-VsSyStEOu90SONlnVpSBOaVnGNBqDUtZTnicQpqnvaPMZHTD1hahbncLhanuqglLpJ7xztPjg8CifJ5zqyC7CAaETj8zbdcoZE8_AdUwQ",
//   "dp": "aXk30r_al9qS_bCMit8VW8MX2VDZdkA37nZY2q6MlaORbOSsh7UT6x2pEofJitdr28KKQl4QFmHPrQu9-d8Kb9dcUs8mTl2857B78ujTnhVmq3tbktmE4Y9BhdJHAqwwgT4XZG1ETdVRj1c0A8czYQfwljvwA3EeOTo_rJMwjIE",
//   "dq": "MF5nH7a7IsKIUlbEbjDIjJt8hc4iwchNed6Kmy84n28FdEhFtkMJjI8wb0OEQJPgDP6Hx8TRj00qz1PArPC7W21cfB41VVCJcNpY8LK7oeiNr7GJ0dCwjZ7F_ViDOmG06RXcgZF5FDSO_n-QqIw6EFb0cVntGKWXtqL1WYW1Vjk",
//   "e": "AQAB",
//   "ext": true,
//   "key_ops": [
//     "sign"
//   ],
//   "kty": "RSA",
//   "n": "x7HC2OMop_3mQVOSE8FdHTIf_aLJDtbz8vSwEnaMBjo-Sl2__FlRcxetooceTop8DnuCmYQjH3YKRPIdkC5yIkHsHj6gLMIQc5BJ5jGJBIaHR83ayzBc2tvX7JgNmee6MWjKWVXLRk-R6Dp0yWVr97oAkqzqQiUPTt45MtCfGnlePTj5XT1otLQ478zgpzBxtk1VaYOhzGEXIrWKG2jKO-CydbsJA5Az4NejqFSpexWk7U6Fy8cjN0B5s_tFlg_GlmHE89_N2SauBmCvcX_Pn4s38ZsxvR9Q2i_I4I4eocZ5ujAq9b0qTnrORHxwLPMV6YANqpz8C6Bnt46o2CerzQ",
//   "p": "-CFjVnhItE7Vv9ELQs3PLMsoMs5GFsS_fwmkhOkvXTzYERyXoS32VZ-fZvK_3QPwhHXmpTq-Qz9e_XoLoVufK2i-NHxAc09MYKYty3GDWBl66HA_8TJWX1niA_fPhm84v7dMf3IMRNNaHi9pZM0Z3HxUc-b9EnHH5G-a57YETEE",
//   "q": "zgcc4unaBcBb7PNNDV1BJW_t6zZ72wwAK1OzF5-xcaKSrGdTM4789w-OkPQtuWNoq6-MkSoNC-zeGVVJ_H79cLiejmdh7SE5F9sorpDTuA7mvzJfj5DC146m62BpvGWMtVbYn7ItBLgkQZko90NL2QQsMmu5oPBmKgo_xEkOrI0",
//   "qi": "Tef18Zw3xJa9HC9eWJNAJB00Sire-FPRrpSxPvUSTyaTSisLr62hUXvWj0j_0SLjLtYdMxkhHGwUez35qC-rLsP5KUTBV9r2-rP7N0gWqbwyBhDAYh7o5WhCfM4TnJmz_nF_uBD_76yD7kGD-eDLqq_p85tOXJBnOHv0_3xmRic"
// }
//
//
// Public key
// NOTE: The public key is a sub-selection of the private key attributes [alg, e, ext, key_ops, kty, n]
// {
//   "alg": "RS256",
//   "e": "AQAB",
//   "ext": true,
//   "key_ops": [
//     "verify"
//   ],
//   "kty": "RSA",
//   "n": "x7HC2OMop_3mQVOSE8FdHTIf_aLJDtbz8vSwEnaMBjo-Sl2__FlRcxetooceTop8DnuCmYQjH3YKRPIdkC5yIkHsHj6gLMIQc5BJ5jGJBIaHR83ayzBc2tvX7JgNmee6MWjKWVXLRk-R6Dp0yWVr97oAkqzqQiUPTt45MtCfGnlePTj5XT1otLQ478zgpzBxtk1VaYOhzGEXIrWKG2jKO-CydbsJA5Az4NejqFSpexWk7U6Fy8cjN0B5s_tFlg_GlmHE89_N2SauBmCvcX_Pn4s38ZsxvR9Q2i_I4I4eocZ5ujAq9b0qTnrORHxwLPMV6YANqpz8C6Bnt46o2CerzQ"
// }
//</editor-fold>