object Modules {
    const val App = ":app"

    object Features {
        private const val prefix = ":features"

        object Base {
            private const val prefix1 = ":base"

            const val Core = "$prefix$prefix1:core"
            const val Navigation = "$prefix$prefix1:navigation"
            const val Elements = "$prefix$prefix1:elements"
        }

        const val Nodes = "$prefix:nodes"
        const val Home = "$prefix:home"
        const val Schedule = "$prefix:schedule"
        const val Account = "$prefix:account"
        const val Misc = "$prefix:misc"
    }

    object Domain {
        private const val prefix = ":domain"

        const val Base = "$prefix:base"
        const val Nodes = "$prefix:nodes"
        const val Schedule = "$prefix:schedule"
        const val Account = "$prefix:account"
    }

    object Data {
        private const val prefix = ":data"

        const val Base = "$prefix:base"
        const val Nodes = "$prefix:nodes"
        const val Schedule = "$prefix:schedule"
        const val Account = "$prefix:account"
    }
}