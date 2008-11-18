/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

class PathResolverTuple {
    private Object nodeCheck;
    private Object indexCheck;

    public PathResolverTuple(Object nodeCheck, Object indexCheck) {
        this.nodeCheck = nodeCheck;
        this.indexCheck = indexCheck;
    }

    public Object getNodeCheck() {
        return nodeCheck;
    }

    public Object getIndexCheck() {
        return indexCheck;
    }

}