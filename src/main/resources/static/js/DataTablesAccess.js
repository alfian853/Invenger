// put jquery and datatables import before import this file

class DTAccess{

    constructor(datatables,row_prefix='row-',column_len){
        this.datatables = datatables;
        this.row_prefix = row_prefix;
        this.column_len = column_len;
    }

    getRowById(id){
        return $('#'+this.row_prefix+id);
    }

    getColById(id,col_name){
        return this.getRowById(id).find('[col=\"'+col_name+'\"]');
    }

    addData(id,data,columns_name){
        if(columns_name.count() > this.column_len){
            throw "columns name count can't be greater than "+this.column_len
        }
        this.datatables.row.add(data)
            .draw()
            .node().id = this.row_prefix + id;

        let target = $(this.row_prefix + id);

        for(let i = 0; i < this.column_len; i++){
            target.find('td:eq('+i+')').attr('col',columns_name[i]);
        }
    }

}
