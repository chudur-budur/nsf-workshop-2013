 (if (not (not is-stuck)) (move open-space)
     (if is-stuck (if is-stuck continue continue)
         (if is-stuck continue continue)))
