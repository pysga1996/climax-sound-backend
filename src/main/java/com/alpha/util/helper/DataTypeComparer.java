package com.alpha.util.helper;

import java.math.BigInteger;

/**
 * @author thanhvt
 * @created 08/08/2021 - 11:35 SA
 * @project vengeance
 * @since 1.0
 **/
public final class DataTypeComparer {

    private DataTypeComparer() {}

    public static boolean equal(BigInteger bigInt1, BigInteger bigInt2) {
        if (bigInt1 == null) return bigInt2 == null;
        return bigInt1.equals(bigInt2);
    }

    public static boolean equal(Long bigInt1, Long bigInt2) {
        if (bigInt1 == null) return bigInt2 == null;
        return bigInt1.equals(bigInt2);
    }

}
