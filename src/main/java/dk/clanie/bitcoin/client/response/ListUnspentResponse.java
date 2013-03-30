/**
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package dk.clanie.bitcoin.client.response;

import org.springframework.roo.addon.javabean.RooJavaBean;

/**
 * Response object returned by listUnspent.
 *
 * Holds an array of {@link ListUnspentResult} objects, each of which has data
 * obaout one ounspent transaction output.
 *
 * @author Claus Nielsen
 */
@SuppressWarnings("serial")
@RooJavaBean(settersByDefault = false)
public class ListUnspentResponse extends BitcoindJsonRpcResponse<ListUnspentResult[]> {

}
