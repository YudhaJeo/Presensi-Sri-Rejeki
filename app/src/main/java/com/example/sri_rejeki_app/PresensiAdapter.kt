import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sri_rejeki_app.Presensi
import com.example.sri_rejeki_app.databinding.ItemPresensiBinding

class PresensiAdapter(private val listPresensi: List<Presensi>) :
    RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder>() {

    inner class PresensiViewHolder(val binding: ItemPresensiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresensiViewHolder {
        val binding = ItemPresensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PresensiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresensiViewHolder, position: Int) {
        val item = listPresensi[position]
        holder.binding.tvStatus.text = item.jenis_presensi
        holder.binding.tvWaktu.text = item.waktu
    }

    override fun getItemCount(): Int = listPresensi.size
}
